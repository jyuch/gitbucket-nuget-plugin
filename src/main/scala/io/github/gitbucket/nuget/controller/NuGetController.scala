package io.github.gitbucket.nuget.controller

import gitbucket.core.api.{ApiPath, JsonFormat}
import gitbucket.core.controller.ControllerBase
import gitbucket.core.util.Implicits._

class NuGetController extends ControllerBase {

  before() {
    contentType = formats("json")
  }

  get("/nuget/v3/index.json") {
    JsonFormat(ServiceIndex("3.0.0", List(Map("@id" -> ApiPath("/nuget/v3/query"), "@type" -> "SearchQueryService"))))
  }

}

case class ServiceIndex(
    version: String,
    resources: List[Map[String, Any]]
)
