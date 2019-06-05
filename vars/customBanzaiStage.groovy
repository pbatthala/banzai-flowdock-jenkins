#!/usr/bin/env groovy
import com.ge.nola.BanzaiCfg
import com.ge.nola.BanzaiStageCfg
import com.ge.nola.BanzaiStepCfg

def call(BanzaiCfg cfg, BanzaiStageCfg stageCfg) {
  String stageName = stageCfg.name
  List<BanzaiStepCfg> stepCfgs = findValueInRegexObject(stageCfg.steps, BRANCH_NAME)

  if (stepCfgs == null) {
    logger "${BRANCH_NAME} does not match a branch pattern for the custom stage '${stageName}'. Skipping ${stageName}"
    return
  }

  stage (stageName) {
    try {
      notify(cfg, [
        scope: BanzaiEvent.scope.STAGE,
        status: BanzaiEvent.status.PENDING,
        stage: stageName,
        message: 'Pending'
      ])

      stepCfgs.each {
          if (it.script) {
              runScript(cfg, it.script)
          } else if (it.closure) {
              it.closure.call(cfg)
          }
      }
      notify(cfg, [
        scope: BanzaiEvent.scope.STAGE,
        status: BanzaiEvent.status.SUCCESS,
        stage: stageName,
        message: 'Success'
      ])
    } catch (err) {
        echo "Caught: ${err}"
        currentBuild.result = 'FAILURE'
        if (isGithubError(err)) {
          notify(cfg, [
            scope: BanzaiEvent.scope.STAGE,
            status: BanzaiEvent.status.FAILURE,
            stage: stageName,
            message: 'githubdown'
          ])
        } else {
          notify(cfg, [
            scope: BanzaiEvent.scope.STAGE,
            status: BanzaiEvent.status.FAILURE,
            stage: stageName,
            message: 'Failed'
          ])   
        }
        
        error(err.message)
    }
  }

}
