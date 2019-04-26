#!/usr/bin/env groovy

// named banzaiBuild to avoid collision with existing 'build' jenkins pipeline plugin
def call(config) {
    runScript(config, "buildScriptFile", "buildScript", [BRANCH_NAME])
}
