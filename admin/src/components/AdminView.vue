<template>
  <h1><comments-logo size="80"/> Admin - Comments </h1>

  <p>
    This is the Admin - Interface for the <comments-logo size="16"/> API.
    Expand the categories to see, delete and reply to comments. Deletions are permanent and not recoverable...
    Categories with unread comments are highlighted as follows: <strong class="unread">!!!</strong>. Replying to
    a comment sets it to the read-state.
  </p>
  <p><button @click="refresh">Refresh</button><button @click="readAllComments">Set all to "read"</button></p>
  <loading-animation :loading="initializing"></loading-animation>
  <h2>Comment Categories / Pages</h2>
  <transition>
  <ul v-if="commentSources.length!=0">
    <li v-for="commentSource in commentSources">
      <a :href="commentSource.source">{{ commentSource.sourceTitle }}</a>
      <strong> #<u>{{ commentSource.count }}</u></strong>
      <button v-if="isCollapsed(commentSource.source)" type="button" @click="commentsToShow = ''" >^</button>
      <button v-else type="button" @click="showComments(commentSource)" >v</button>
      <strong class="unread" v-if="commentSource.hasUnreads">!!!</strong>
      <loading-animation :loading="loading" size="1"></loading-animation>

      <transition name="slide-fade">
        <ul v-if="isCollapsed(commentSource.source)">
          <li v-for="commentToEdit in commentsToEdit">
            <div class="emphasize user">

              <strong>{{ commentToEdit.user }}</strong>:
              <br v-if="commentToEdit.comment.includes('<br>')"> <em v-html="commentToEdit.comment"></em>
              <button type="button" @click="readComment(commentToEdit.id, commentSource)">
                {{ commentToEdit.read ? 'read' : 'unread' }}
                <strong class="unread" v-if="!commentToEdit.read">!!!</strong></button>
              <button type="button" @click="deleteComment(commentToEdit.id, commentSource)">delete</button>
              <button v-if="commentToReply !== commentToEdit.id" type="button" @click="commentToReply = commentToEdit.id">
                <span v-if="commentToEdit.reply">edit</span>reply</button>
              <button v-else type="button" @click="commentToReply = ''">cancel reply form</button>
            </div>
            <transition name="slide-fade">
              <reply-form
                :already-replied="commentToEdit.reply ? commentToEdit.reply.replaceAll('<br>', '\n'): ''"
                @addReply="addReply(commentSource, $event)"
                v-if="commentToReply === commentToEdit.id"/>
            </transition>
            <div class="emphasize admin" v-if="commentToEdit.reply">
              <strong>Replied: </strong><em v-html="commentToEdit.reply"></em>
            </div>
          </li>
        </ul>
      </transition>
    </li>
  </ul>
  </transition>
</template>

<script>
import axios from 'axios'
import { ref, onMounted } from 'vue'
import LoadingAnimation from '@/components/LoadingAnimation'
import ReplyForm from '@/components/ReplyForm'
import CommentsLogo from '@/components/CommentsLogo'
export default {
  name: 'AdminView',
  components: {
    LoadingAnimation,
    ReplyForm,
    CommentsLogo
  },
  setup () {
    const baseUrl = process.env.BASE_URL + 'secapi/'
    const commentSources = ref([])
    const commentsToShow = ref('')
    const commentsToEdit = ref([])
    const commentToReply = ref('')
    const loading = ref(false)
    const initializing = ref(false)

    let commentsUnread = []

    onMounted(() => prepare())

    function prepare () {
      initializing.value = true
      axios.get(baseUrl + 'commentCategories')
        .then(r => {
          commentSources.value = r.data
          prepareUnreadHighlighting()
        })
        .then(() => {
          initializing.value = false
        })
    }

    function prepareUnreadHighlighting () {
      return axios.get(baseUrl + 'commentsUnread')
        .then(r => {
          commentsUnread = r.data
          commentSources.value.forEach(commentSource => {
            commentSource.hasUnreads = false
            commentsUnread.forEach(commentUnread => {
              if (commentSource.source === commentUnread.source) commentSource.hasUnreads = true
            })
          })
        })
    }

    function refresh () {
      commentSources.value = []
      commentsToShow.value = ''
      prepare()
    }

    function isCollapsed (source) {
      return commentsToShow.value === source
    }

    function showComments (source) {
      loading.value = true
      axios.get(baseUrl + 'comments?source=' + source.source).then(
        r => {
          loading.value = false
          commentsToShow.value = source.source
          commentsToEdit.value = r.data
        }
      )
    }

    function deleteComment (id, commentSource) {
      loading.value = true
      axios.post(baseUrl + 'delete?id=' + id).then(
        () => {
          commentSources.value.forEach(s => {
            if (s.source === commentSource.source) s.count--
          })
          if (commentSource.count === 0) {
            loading.value = false
            prepare()
          } else showComments(commentSource)
        }
      )
    }

    function addReply (source, replyText) {
      loading.value = true
      const requestBody = { text: replyText, id: commentToReply.value }
      axios.post(baseUrl + 'reply', requestBody)
        .then(() => {
          commentToReply.value = ''
          prepareUnreadHighlighting()
          showComments(source)
        }
        )
    }

    function readComment (id, commentSource) {
      loading.value = true
      axios.post(baseUrl + 'read?id=' + id)
        .then(() => prepareUnreadHighlighting())
        .then(() => showComments(commentSource))
    }

    function readAllComments () {
      axios.post(baseUrl + 'readAll')
        .then(() => {
          prepareUnreadHighlighting()
          commentsToEdit.value.forEach(c => c.read = true)
        })
    }

    return {
      commentSources,
      commentsToShow,
      commentsToEdit,
      commentToReply,
      loading,
      initializing,
      refresh,
      showComments,
      isCollapsed,
      deleteComment,
      readComment,
      readAllComments,
      addReply
    }
  }
}
</script>
