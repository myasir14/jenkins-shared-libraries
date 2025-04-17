def call(Map config = [:]) {
    def generateReports = config.generateReports ?: true
    def archiveResults = config.archiveResults ?: true
    
    echo "Running E-commerce App Tests..."
    
    try {
        // Install dependencies first
        sh '''
            npm install
            
            # Run the test suites
            npm run test:unit
            npm run test:integration
            npm run test:a11y
            
            # Generate coverage report
            npm run test:coverage
        '''
        
        if (generateReports) {
            // Publish test results if junit reports exist
            if (fileExists('**/junit.xml')) {
                junit '**/junit.xml'
            }
            
            // Publish coverage report if it exists
            if (fileExists('coverage/index.html')) {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'coverage',
                    reportFiles: 'index.html',
                    reportName: 'Coverage Report',
                    reportTitles: ''
                ])
            }
        }
        
        if (archiveResults) {
            // Archive test artifacts
            archiveArtifacts artifacts: 'coverage/**/*', fingerprint: true, allowEmptyArchive: true
        }
        
    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        error "Test execution failed: ${e.message}"
    }
}
