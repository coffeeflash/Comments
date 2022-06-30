<template>
  <div class="emphasize">
    <label>Reply:</label><br>
    <textarea v-model="replyText"></textarea><br><br>
    <button :disabled="inputValidation" @click="addReply">Add Reply</button>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
export default {
  name: "ReplyForm",
  props: { alreadyReplied: { default: ''} },
  setup(props, context){
    const replyText = ref(props.alreadyReplied)
    const inputValidation = computed(() => !replyText.value)

    function addReply(){
      context.emit('addReply', replyText.value)
    }

    return {
      addReply,
      inputValidation,
      replyText
    }
  }
}
</script>

<style scoped>

textarea {
  padding: 0;
  height: 6rem;
  width: 100%;
}

</style>
