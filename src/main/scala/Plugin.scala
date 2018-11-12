import io.github.gitbucket.nuget.controller.NuGetController
import io.github.gitbucket.solidbase.model.Version

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "nuget"
  override val pluginName: String = "NuGet Repository Plugin"
  override val description: String = "Provide NuGet repository"
  override val versions: List[Version] = List(new Version("1.0.0"))

  override val controllers = Seq(
    "/*" -> new NuGetController()
  )
}
