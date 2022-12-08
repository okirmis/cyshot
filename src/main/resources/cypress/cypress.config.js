const { defineConfig } = require('cypress')

module.exports = defineConfig({
    e2e: {
        supportFile: './commands.js',
        specPattern: './*.spec.js',
        video: false,
        screenshotsFolder: 'images',
        screenshotOnRunFailure: false,
        trashAssetsBeforeRuns: false
    }
})
