// vars/runTests.groovy

def call(Map config) {
    // Validate required parameters
    if (!config.tier) {
        error("Test tier must be specified: 'frontend', 'backend', or 'database'")
    }

    // Tier-specific test execution
    switch (config.tier.toLowerCase()) {
        case 'frontend':
            runFrontendTests()
            break
        case 'backend':
            runBackendTests()
            break
        case 'database':
            runDatabaseTests()
            break
        default:
            error("Invalid tier: ${config.tier}. Use 'frontend', 'backend', or 'database'")
    }
}

// Frontend Test Logic
def runFrontendTests() {
    echo "Running Frontend Tests (Next.js, Redux, Tailwind)..."
    try {
        sh """
            cd frontend
            npm install
            npm run test:unit       # Jest/React Testing Library
            npm run test:integration # Cypress/Selenium
            npm run test:accessibility # Axe-core for a11y
        """
        junit 'frontend/test-results/**/*.xml'  // Publish JUnit reports
    } catch (err) {
        error("Frontend tests failed: ${err.message}")
    }
}

// Backend Test Logic
def runBackendTests() {
    echo "Running Backend Tests (Next.js API, Auth, Validation)..."
    try {
        sh """
            cd backend
            npm install
            npm run test:unit       # Jest/Supertest for API routes
            npm run test:integration # Postman/Newman
            npm run test:security    # OWASP ZAP scans
        """
        junit 'backend/test-results/**/*.xml'
    } catch (err) {
        error("Backend tests failed: ${err.message}")
    }
}

// Database Test Logic
def runDatabaseTests() {
    echo "Running Database Tests (MongoDB, Mongoose)..."
    try {
        sh """
            cd backend
            npm run test:db         # Jest + Mongoose in-memory DB
            npm run test:validation # Schema validation tests
        """
        junit 'backend/db-test-results/**/*.xml'
    } catch (err) {
        error("Database tests failed: ${err.message}")
    }
}
