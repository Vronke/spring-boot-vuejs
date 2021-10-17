const fs = require('fs');

module.exports = {
  "devServer": {
    "disableHostCheck" : true,
    "port" : 443,
    "https": {
      "key" : fs.readFileSync('./certs/key.key'),
      "cert" : fs.readFileSync('./certs/cert.crt')
    },
    "proxy": {
      "/api": {
        "target": "https://localhost:8080",
        "ws": true,
        "changeOrigin": true
      },
      "/product": {
        "target": "https://localhost:8080",
        "ws": true,
        "changeOrigin": true
      },
      "/gs-guide-websocket": {
        "target": "https://localhost:8080",
        "ws": true,
        "changeOrigin": true
      },
      "/sockjs-node": {
        "target": "http://localhost:8080",
        "ws": true,
        "changeOrigin": true
      }
    },
    clientLogLevel: 'error'
  },
  "outputDir": "target/dist",
  "assetsDir": "static",
  "transpileDependencies": [
    "vuetify"
  ]
}