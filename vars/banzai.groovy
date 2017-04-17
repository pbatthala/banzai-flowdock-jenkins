#!/usr/bin/env groovy

def call(body) {
  // evaluate the body block, and collect configuration into the object
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  env.GITHUB_API_URL = 'https://github.build.ge.com/api/v3'

  node {
    currentBuild.result = 'SUCCESS'
    echo "My branch is: ${BRANCH_NAME}"

    // checkout the branch that triggered the build
    checkoutSCM(config)
    // for some reason SCM marks PR as complete so we have to ovveride

    if (config.sast) {
      try {
        notifyGit(config, 'SAST Pending', 'PENDING')
        sast(config)
        notifyGit(config, 'SAST Complete', 'SUCCESS')
      } catch (err) {
        echo "Caught: ${err}"
        currentBuild.result = 'FAILURE'
        throw err
      }
    }

    if (config.build) {
      try {
        notifyGit(config, 'Build Pending', 'PENDING')
        build(config)
        notifyGit(config, 'Build Complete', 'SUCCESS')
      } catch (err) {
        echo "Caught: ${err}"
        currentBuild.result = 'FAILURE'
        throw err
      }
    }

    if (config.publish) {
      try {
        publish(config)
      } catch (err) {
        echo "Caught: ${err}"
        currentBuild.result = 'FAILURE'
        throw err
      }
    }



  } // node

}
