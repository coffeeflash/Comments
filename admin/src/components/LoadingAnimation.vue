<template>
<span v-show="props.loading" :style="'font-size:' + props.size + 'rem;font-weight: bold'">{{spinner}}</span>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: "LoadingAnimation",
  props: {
    size: {
      default: 12
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const spinner = ref('|')
    const spinnerStates = ['/', '-', '\\', '|']
    let i = 0

    // onMounted(() => {
    //   if (props.loading) recursiveWait()
    // })
    watch(props, () => recursiveWait())

    async function recursiveWait() {
      spinner.value = spinnerStates[i++%4]
      console.log('loading' + i)
      await delay(200)
      if(props.loading) await recursiveWait()
      else i = 0
    }

    function delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    }

    return {
      spinner,
      props
    }
  }
}
</script>

<style scoped>
span {
  margin-left: 2rem;
}
</style>
