package io.github.gitbucket.nuget.controller

import gitbucket.core.api.{ApiPath, JsonFormat}
import gitbucket.core.controller.ControllerBase
import gitbucket.core.model.ReleaseTag
import gitbucket.core.service.{AccountService, ReleaseService, RepositoryService}
import gitbucket.core.util.Implicits._

class NuGetController
  extends ControllerBase
    with RepositoryService
    with ReleaseService
    with AccountService {

  import NuGetController._

  get("/nuget/:owner/v3/index.json") {
    contentType = formats("json")
    val owner = params("owner")
    JsonFormat(ServiceIndex("3.0.0",
      List(
        Resource(ApiPath(s"/nuget/$owner/v3/query"), "SearchQueryService"))))
  }

  get("/nuget/:owner/v3/query") {
    contentType = formats("json")
    val owner = params("owner")
    val query = params.get("q")

    val repos = query match {
      case Some(q) =>
        getRepositoryNamesOfUser(owner)
          .map(it => (it, it.toLowerCase())).filter({ it =>
          it match {
            case (l, r) => r.contains(q.toLowerCase)
          }
        }).map(it => it._1)
      case None =>
        getRepositoryNamesOfUser(owner)
    }

    val packages = repos.map(it => A(it, getReleases(owner, it).toList)).filter(it => it.releaseTags.nonEmpty).map(getPackageResult)

    JsonFormat(packages)
  }

  def getLatestReleaseVersion(pack: A): String = {
    pack.releaseTags.sortBy(_.registeredDate).reverse.head.tag
  }

  def getPackageResult(pack: A): Package = {
    Package(pack.repository, getLatestReleaseVersion(pack), pack.releaseTags.map(it => Version("", 0, it.tag)))
  }

}

object NuGetController {

  case class ServiceIndex(
      version: String,
      resources: List[Resource]
  )

  case class Resource(
      `@id`: ApiPath,
      `@type`: String
  )

  case class SearchResultRoot(
      totalHits: Int,
      data: List[Package]
  )

  case class Package(
      id: String,
      version: String,
      versions: List[Version]
  )

  case class Version(
      `@id`: String,
      downloads: Int,
      version: String
  )

  case class A(
      repository: String,
      releaseTags: List[ReleaseTag]
  )

}
