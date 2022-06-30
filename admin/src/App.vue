<template>
  <admin-view/>
</template>

<script>
import AdminView from '@/components/AdminView.vue'

export default {
  name: 'App',
  components: {
    AdminView
  }
}
</script>

<style lang="scss">
// -------------- THEME SWITCHER -------------- //
@mixin dark-appearance {
  filter: invert(1);
  img, svg{
    filter: invert(1);

    &.ioda { filter: invert(0); }
  }
}

body[a="dark"] { @include dark-appearance; }


@media (prefers-color-scheme: dark) {
  body { @include dark-appearance; }
}
// -------------------------------------------- //


// bg color is also needed in html in order to
// block body's background propagation
// see: https://stackoverflow.com/a/61265706
html, body { background: white;}
html{
  height: 100%;
  overflow-y: auto !important; // otherwise vue forces it for some reason...
}

body {
  color: black;
  font-family: monospace;
  font-size: 16px;
  line-height: 1.4;
  margin: 0;
  //padding: 5vh 0 0 0;
  //padding: 10vh 0 0 10vw;
  min-height: 100%;
  overflow-wrap: break-word;
  //text-align: center;
  display: grid;
  grid: auto / 1fr minmax(250px, 720px) 1fr;
}

h1, div {
  grid-column: 2 / 3;
}
body > div{
  padding: 2%;
  border-right: solid 1px lightgray;
  border-left: solid 1px lightgray;
}
.post-meta { text-align: right; }

h2, h3, h4, h5, h6 { margin-top: 3rem; }

hr { margin: 2rem 0; }

p { margin: 1rem 0; }

li { margin: 0.4rem 0; }

*:target { background: yellow; }

.w {
  // max-width: 640px;
  max-width: 720px;
  // max-width: 960px;
  margin: 0 auto;
  padding: 4rem 2rem;
}

hr {
  text-align: center;
  border: 0;

  &:before { content: '/////' }
  &:after { content: attr(data-content) '/////' }
}

table { width: 100%; }

table, th, td {
  border: thin solid black;
  border-collapse: collapse;
  padding: 0.4rem;
}

code {
  color: white;
  background: black;
}

div.highlighter-rouge code {
  display: block;
  overflow-x: auto;
  white-space: pre-wrap;
  padding: 1rem;
}

blockquote {
  font-style: italic;
  border: thin solid black;
  padding: 1rem;

  p { margin: 0; }
}

img {
  max-width: 100%;
  display: block;
  margin: 0 auto;
}

button {
  margin-left: 0.5rem;
}

.emphasize {
  margin-top: 0.6rem;
  padding: 0.5rem;
  background-color: lightgray;
  border-radius: 5px;
}

</style>
