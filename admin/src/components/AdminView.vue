<template>
  <h1><comments-logo size="80"/> Admin - Comments </h1>

  <p>
    This is the Admin - Interface for the <comments-logo size="16"/> API.
      Expand the categories to see, delete and reply to comments. Deletions are permanent and not recoverable...
  </p>
  <loading-animation :loading="initializing"></loading-animation>
  <h2>Comment Categories</h2>
  <ul>
    <li v-for="commentSource in commentSources">
      <a :href="commentSource.source">{{ commentSource.sourceTitle }}</a>
      <strong> #<u>{{ commentSource.count }}</u></strong>
      <button v-if="isCollapsed(commentSource.source)" type="button" @click="commentsToShow = ''" >^</button>
      <button v-else type="button" @click="showComments(commentSource.source)" >v</button>
      <loading-animation :loading="loading" size="1"></loading-animation>
      <ul v-if="isCollapsed(commentSource.source)">
        <li v-for="commentToEdit in commentsToEdit">
          <div class="emphasize">
          <strong>{{ commentToEdit.user }}</strong>:
          <br v-if="commentToEdit.comment.includes('<br>')"> <em v-html="commentToEdit.comment"></em>
          <button type="button" @click="deleteComment(commentToEdit.id, commentSource)">delete</button>
          <button v-if="commentToReply !== commentToEdit.id" type="button" @click="commentToReply = commentToEdit.id">
            <span v-if="commentToEdit.reply">edit</span> reply
          </button>
          <button v-else type="button" @click="commentToReply = ''">cancel reply form</button>
          </div>
          <reply-form
            :already-replied="commentToEdit.reply ? commentToEdit.reply.replaceAll('<br>', '\n'): ''"
            @addReply="addReply(commentSource.source, $event)"
            v-if="commentToReply === commentToEdit.id"/>
          <div class="emphasize admin" v-if="commentToEdit.reply">
            <strong>Replied: </strong><em v-html="commentToEdit.reply"></em>
          </div>
        </li>
      </ul>
    </li>
  </ul>
</template>

<script>
import axios from 'axios'
import  { ref, onMounted } from 'vue'
import LoadingAnimation from "@/components/LoadingAnimation"
import ReplyForm from "@/components/ReplyForm"
import CommentsLogo from "@/components/CommentsLogo";
export default {
  name: 'AdminView',
  components: {
    LoadingAnimation,
    ReplyForm,
    CommentsLogo
  },
  setup(){

    const baseUrl = process.env.BASE_URL + 'secapi/'
    const commentSources = ref([])
    const commentsToShow = ref('')
    const commentsToEdit = ref([])
    const commentToReply = ref('')
    const loading = ref(false)
    const initializing = ref(false)

    onMounted(() => prepare())

    function prepare(){
      initializing.value = true
      axios.get(baseUrl + 'commentCategories').then(
        r => {
          commentSources.value = r.data
          initializing.value = false
        }
      )
    }

    function isCollapsed(source){
      return commentsToShow.value === source
    }

    function showComments(source){
      loading.value = true
      axios.get(baseUrl + 'comments?source=' + source).then(
        r => {
          loading.value = false
          commentsToShow.value = source
          commentsToEdit.value = r.data
        }
      )
    }

    function deleteComment(id, commentSource){
      loading.value = true
      axios.post(baseUrl + 'delete?id=' + id).then(
        () => {
          commentSources.value.forEach(s => {
            if (s.source === commentSource.source) s.count--
          })
          if(commentSource.count == 0) {
            loading.value = false
            prepare()
          }
          else showComments(commentSource.source)
        }
      )
    }

    function addReply(source, replyText){
      loading.value = true
      const requestBody = {text: replyText, id: commentToReply.value}
      axios.post(baseUrl + 'reply', requestBody).then(
        () => {
          commentToReply.value = ''
          showComments(source)
        }
      )
    }

    return {
      commentSources,
      commentsToShow,
      commentsToEdit,
      commentToReply,
      loading,
      initializing,
      showComments,
      isCollapsed,
      deleteComment,
      addReply
    }
  }
}
</script>

