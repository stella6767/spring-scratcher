window.swaggerSpec={
  "openapi" : "3.0.1",
  "info" : {
    "title" : "API 문서",
    "description" : "RestDocsWithSwagger Docs",
    "version" : "0.0.1"
  },
  "servers" : [ {
    "url" : "http://localhost:8080"
  } ],
  "tags" : [ ],
  "paths" : { },
  "components" : {
    "schemas" : { },
    "securitySchemes" : {
      "APIKey" : {
        "type" : "apiKey",
        "name" : "Authorization",
        "in" : "header"
      }
    }
  },
  "security" : [ {
    "APIKey" : [ ]
  } ]
}