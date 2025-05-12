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
  "paths" : {
    "/todo/list" : {
      "get" : {
        "tags" : [ "todo" ],
        "summary" : "B2B API",
        "description" : "B2B API",
        "operationId" : "todo-controller-test/find-todos",
        "responses" : {
          "200" : {
            "description" : "200",
            "content" : {
              "application/json;charset=UTF-8" : {
                "schema" : {
                  "$ref" : "#/components/schemas/todo-list486549215"
                },
                "examples" : {
                  "todo-controller-test/find-todos" : {
                    "value" : "{\n  \"resultMsg\" : \"find todos\",\n  \"data\" : {\n    \"content\" : [ {\n      \"id\" : 7131,\n      \"content\" : \"OPXNIWME\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : false,\n      \"createdAt\" : \"WSXJNY\",\n      \"updatedAt\" : \"SQWYDQU\"\n    }, {\n      \"id\" : 188,\n      \"content\" : \"CYORMLVOUM\",\n      \"status\" : \"NORMAL\",\n      \"isFinish\" : true,\n      \"createdAt\" : \"WMKMKITUM\",\n      \"updatedAt\" : \"DVE\"\n    }, {\n      \"id\" : 1856,\n      \"content\" : \"XYSIVFHPI\",\n      \"status\" : \"NORMAL\",\n      \"isFinish\" : false,\n      \"createdAt\" : \"IMABL\",\n      \"updatedAt\" : \"DEUYBGKH\"\n    }, {\n      \"id\" : 9745,\n      \"content\" : \"WWHDYXHW\",\n      \"status\" : \"MINOR\",\n      \"isFinish\" : false,\n      \"createdAt\" : \"BUNT\",\n      \"updatedAt\" : \"MUIOJEZTB\"\n    }, {\n      \"id\" : 840,\n      \"content\" : \"AMNBBWZD\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : true,\n      \"createdAt\" : \"CBKFNS\",\n      \"updatedAt\" : \"MVTGNNI\"\n    }, {\n      \"id\" : 9633,\n      \"content\" : \"UGJ\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : false,\n      \"createdAt\" : \"CTPUPCZ\",\n      \"updatedAt\" : \"CRMPF\"\n    }, {\n      \"id\" : 6463,\n      \"content\" : \"GWEAHJPRNA\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : true,\n      \"createdAt\" : \"MWXGPHNGU\",\n      \"updatedAt\" : \"EWOJRYR\"\n    }, {\n      \"id\" : 7450,\n      \"content\" : \"UGR\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : false,\n      \"createdAt\" : \"UWTSSBPGU\",\n      \"updatedAt\" : \"RIYKLP\"\n    }, {\n      \"id\" : 6409,\n      \"content\" : \"HXQKEVJF\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : true,\n      \"createdAt\" : \"QSGFJJJB\",\n      \"updatedAt\" : \"IQZWR\"\n    }, {\n      \"id\" : 9400,\n      \"content\" : \"TYPPSSUM\",\n      \"status\" : \"URGENT\",\n      \"isFinish\" : true,\n      \"createdAt\" : \"HVWKTEZSO\",\n      \"updatedAt\" : \"ANOS\"\n    } ],\n    \"pageable\" : {\n      \"pageNumber\" : 0,\n      \"pageSize\" : 10,\n      \"sort\" : {\n        \"empty\" : true,\n        \"unsorted\" : true,\n        \"sorted\" : false\n      },\n      \"offset\" : 0,\n      \"unpaged\" : false,\n      \"paged\" : true\n    },\n    \"last\" : false,\n    \"totalPages\" : 10,\n    \"totalElements\" : 100,\n    \"first\" : true,\n    \"size\" : 10,\n    \"number\" : 0,\n    \"sort\" : {\n      \"empty\" : true,\n      \"unsorted\" : true,\n      \"sorted\" : false\n    },\n    \"numberOfElements\" : 10,\n    \"empty\" : false\n  }\n}"
                  }
                }
              }
            }
          }
        }
      }
    },
    "/todo/todo" : {
      "post" : {
        "tags" : [ "todo" ],
        "summary" : "B2B API",
        "description" : "B2B API",
        "operationId" : "todo-controller-test/save-todo",
        "requestBody" : {
          "content" : {
            "application/json;charset=UTF-8" : {
              "schema" : {
                "$ref" : "#/components/schemas/todo-list486549215"
              },
              "examples" : {
                "todo-controller-test/save-todo" : {
                  "value" : "{\n  \"content\" : \"QHL\",\n  \"status\" : \"MINOR\"\n}"
                }
              }
            }
          }
        },
        "responses" : {
          "200" : {
            "description" : "200",
            "content" : {
              "application/json;charset=UTF-8" : {
                "schema" : {
                  "$ref" : "#/components/schemas/todo-list486549215"
                },
                "examples" : {
                  "todo-controller-test/save-todo" : {
                    "value" : "{\n  \"resultMsg\" : \"save todo\",\n  \"data\" : {\n    \"id\" : 5246,\n    \"content\" : \"WEXMPD\",\n    \"status\" : \"URGENT\",\n    \"isFinish\" : true,\n    \"createdAt\" : \"TPSPBONW\",\n    \"updatedAt\" : \"LHV\"\n  }\n}"
                  }
                }
              }
            }
          }
        }
      }
    },
    "/todo/{id}" : {
      "get" : {
        "tags" : [ "todo" ],
        "summary" : "B2B API",
        "description" : "B2B API",
        "operationId" : "todo-controller-test/find-todo-by-id",
        "parameters" : [ {
          "name" : "id",
          "in" : "path",
          "description" : "",
          "required" : true,
          "schema" : {
            "type" : "string"
          }
        } ],
        "responses" : {
          "200" : {
            "description" : "200",
            "content" : {
              "application/json;charset=UTF-8" : {
                "schema" : {
                  "$ref" : "#/components/schemas/todo-list486549215"
                },
                "examples" : {
                  "todo-controller-test/find-todo-by-id" : {
                    "value" : "{\n  \"resultMsg\" : \"find todos\",\n  \"data\" : {\n    \"id\" : 841,\n    \"content\" : \"VQBDPGGA\",\n    \"status\" : \"MINOR\",\n    \"isFinish\" : false,\n    \"createdAt\" : \"XELGXRE\",\n    \"updatedAt\" : \"XRWLB\"\n  }\n}"
                  }
                }
              }
            }
          }
        }
      },
      "put" : {
        "tags" : [ "todo" ],
        "summary" : "B2B API",
        "description" : "B2B API",
        "operationId" : "todo-controller-test/update-todo",
        "parameters" : [ {
          "name" : "id",
          "in" : "path",
          "description" : "",
          "required" : true,
          "schema" : {
            "type" : "string"
          }
        } ],
        "requestBody" : {
          "content" : {
            "application/json;charset=UTF-8" : {
              "schema" : {
                "$ref" : "#/components/schemas/todo-list486549215"
              },
              "examples" : {
                "todo-controller-test/update-todo" : {
                  "value" : "{\n  \"id\" : 3497,\n  \"content\" : \"SYXBTTG\",\n  \"status\" : \"NORMAL\",\n  \"isFinish\" : true\n}"
                }
              }
            }
          }
        },
        "responses" : {
          "200" : {
            "description" : "200",
            "content" : {
              "application/json;charset=UTF-8" : {
                "schema" : {
                  "$ref" : "#/components/schemas/todo-list486549215"
                },
                "examples" : {
                  "todo-controller-test/update-todo" : {
                    "value" : "{\n  \"resultMsg\" : \"update todo\",\n  \"data\" : {\n    \"id\" : 2623,\n    \"content\" : \"TXYMCCHOLE\",\n    \"status\" : \"NORMAL\",\n    \"isFinish\" : false,\n    \"createdAt\" : \"FZBVCJN\",\n    \"updatedAt\" : \"ILIAH\"\n  }\n}"
                  }
                }
              }
            }
          }
        }
      }
    },
    "/todo/todo/{id}" : {
      "delete" : {
        "tags" : [ "todo" ],
        "summary" : "B2B API",
        "description" : "B2B API",
        "operationId" : "todo-controller-test/delete-todo-by-id",
        "parameters" : [ {
          "name" : "id",
          "in" : "path",
          "description" : "",
          "required" : true,
          "schema" : {
            "type" : "string"
          }
        } ],
        "responses" : {
          "200" : {
            "description" : "200",
            "content" : {
              "application/json;charset=UTF-8" : {
                "schema" : {
                  "$ref" : "#/components/schemas/todo-list486549215"
                },
                "examples" : {
                  "todo-controller-test/delete-todo-by-id" : {
                    "value" : "{\n  \"resultMsg\" : \"delete todo by id\",\n  \"data\" : { }\n}"
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "components" : {
    "schemas" : {
      "todo-list486549215" : {
        "type" : "object"
      }
    },
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