const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  assetsDir: 'static',
  devServer: {
    proxy: {
      '/comments/secapi': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  publicPath: '/comments'

})
