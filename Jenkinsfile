@Library('jenkinsfile_stdlib') _

node {
    properties([
        pipelineTriggers([
            [$class: 'GenericTrigger',
                genericVariables: [
                    [expressionType: 'JSONPath', key: 'REVIEWBOARD_REVIEW_URL', value: '$.review_request.absolute_url'],
                    [expressionType: 'JSONPath', key: 'REVIEWBOARD_REVIEW_ID', value: '$.review_request.id'],
                    [expressionType: 'JSONPath', key: 'USERNAME', value: '$.review_request.links.submitter.title'],
                    [expressionType: 'JSONPath', key: 'GIT_REPOSITORY', value: '$.review_request.links.repository.title'],
                    [expressionType: 'JSONPath', key: 'GIT_BRANCH', value: '$.review_request.branch'],
                    [expressionType: 'JSONPath', key: 'COMMIT_SHA', value: '$.review_request.commit_id']
                ],
                genericRequestVariables: [],
                genericHeaderVariables: [],
                regexpFilterText: '$GIT_REPOSITORY',
                regexpFilterExpression: 'android-bento'
            ]
        ])
    ])

    stage("Test") {
        if (env.GIT_REPOSITORY && env.GIT_BRANCH) {
           build job: 'Bento-Checks', parameters: [
              [$class: 'StringParameterValue', name: 'LOCAL_BRANCH', value: env.GIT_BRANCH],
              [$class: 'StringParameterValue', name: 'GIT_COMMIT', value: env.COMMIT_SHA],
              [$class: 'StringParameterValue', name: 'REVIEW_ID', value: env.REVIEWBOARD_REVIEW_ID],
              [$class: 'StringParameterValue', name: 'USER_ID', value: env.USERNAME],
              [$class: 'StringParameterValue', name: 'USER_EMAIL', value: env.USERNAME + "@yelp.com"],
           ]
        }
    }
}
