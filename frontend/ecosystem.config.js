module.exports = {
    apps: [{
        name: "updowngg-frontend",
        script: "serve",  // npm install -g serve 필요
        env: {
            PM2_SERVE_PATH: "./dist",
            PM2_SERVE_PORT: 4173,
            PM2_SERVE_SPA: "true",
            PM2_SERVE_HOMEPAGE: "/index.html"
        }
    }]
}