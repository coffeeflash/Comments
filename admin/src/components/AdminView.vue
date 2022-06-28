<template>
    <h1>Admin - Comments</h1>
    <p>
      For a guide and recipes on how to configure / customize this project,<br>
      check out the
      <a href="https://cli.vuejs.org" target="_blank" rel="noopener">vue-cli documentation</a>.
    </p>
    <h2>Installed CLI Plugins</h2>
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

        <ul v-if="isCollapsed(commentSource.source)">
          <li v-for="commentToEdit in commentsToEdit">
            {{commentToEdit.comment}}
            <button type="button" @click="deleteComment(commentToEdit.id, commentSource.source)">delete</button>
          </li>
        </ul>

      </li>
    </ul>
</template>

<script>
import axios from 'axios'
import  { ref, onMounted } from 'vue'

export default {
  name: 'AdminView',
  setup(){

    const baseUrl = process.env.BASE_URL + 'secapi/'
    const commentSources = ref([])
    const commentsToShow = ref('')
    const commentsToEdit = ref([])

    onMounted(() => prepare())

    function prepare(){
      axios.get(baseUrl + 'commentCategories').then(
        r => {
          commentSources.value = r.data
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
      axios.get(baseUrl + 'comments?source=' + source).then(
        r => {
          commentsToShow.value = source
          commentsToEdit.value = r.data
        }
      )
    }

    function deleteComment(id, source){
      axios.post(baseUrl + 'delete?id=' + id).then(
        () => {
          showComments(source)
        }
      )
    }

    return {
      commentSources,
      commentsToShow,
      commentsToEdit,
      makeUrl,
      showComments,
      isCollapsed,
      deleteComment
    }
  }
}
</script>
