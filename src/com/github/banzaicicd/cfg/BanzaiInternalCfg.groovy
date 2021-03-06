package com.github.banzaicicd.cfg;

class BanzaiInternalGitOpsCfg {
    String TARGET_ENV
    String TARGET_STACK
    Map<String, String> SERVICE_VERSIONS_TO_UPDATE
    Boolean DEPLOY = false
    List<String> DEPLOY_ARGS
    String DEPLOYMENT_ID
}

class BanzaiInternalCfg {
    Boolean PIPELINE_FAILED = false
    BanzaiInternalGitOpsCfg gitOps = new BanzaiInternalGitOpsCfg()
}