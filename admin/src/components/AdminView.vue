<template xmlns="http://www.w3.org/1999/html">
    <h1>Admin - Comments</h1>
    <p>
      An overview of all the comments.<br>
      Expand the categories to see, delete and reply to comments.
    </p>
    <LoadingAnimation :loading="initializing"></LoadingAnimation>
    <h2>Comment Categories</h2>
    <ul>
      <li v-for="commentSource in commentSources">
        <a :href="makeUrl(commentSource.source)"> {{ commentSource.source }} </a>
        has {{ commentSource.count }} comments
        <button v-if="isCollapsed(commentSource.source)" type="button" @click="commentsToShow = ''" >
          ^
        </button>
        <button v-else type="button" @click="showComments(commentSource.source)" >
          v
        </button>
        <LoadingAnimation :loading="loading" size="1"></LoadingAnimation>
        <ul v-if="isCollapsed(commentSource.source)">
          <li v-for="commentToEdit in commentsToEdit">
            {{commentToEdit.comment}}
            <button type="button" @click="deleteComment(commentToEdit.id, commentSource)">delete</button>
          </li>
        </ul>

      </li>
    </ul>
</template>

<script>
import axios from 'axios'
import  { ref, onMounted } from 'vue'
import LoadingAnimation from "@/components/LoadingAnimation";
export default {
  name: 'AdminView',
  components: {
    LoadingAnimation
  },
  setup(){

    const baseUrl = process.env.BASE_URL + 'secapi/'
    const commentSources = ref([])
    const commentsToShow = ref('')
    const commentsToEdit = ref([])
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

    function makeUrl(source){
      source = source.replaceAll(" ", "-")
      source = source.toLowerCase()
      return source + ".html"
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

    return {
      commentSources,
      commentsToShow,
      commentsToEdit,
      loading,
      initializing,
      makeUrl,
      showComments,
      isCollapsed,
      deleteComment
    }
  }
}
</script>
