import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._

ThisBuild / organization := "org.openurp.std.graduation"
ThisBuild / version := "0.0.2"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-graduation"),
    "scm:git@github.com:openurp/std-graduation.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std graduation"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-graduation/index.html"))

val apiVer = "0.34.3"
val starterVer = "0.3.10"
val baseVer = "0.4.8"
val eduCoreVer="0.0.13"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer
val openurp_edu_grade_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer

lazy val root = (project in file("."))
  .settings()
  .aggregate(core,web,audit,archive,webapp)

lazy val core = (project in file("core"))
  .settings(
    name := "openurp-std-graduation-core",
    common,
    libraryDependencies ++= Seq(openurp_edu_api,openurp_std_api,beangle_ems_app)
  )

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-std-graduation-web",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag),
    libraryDependencies ++= Seq(beangle_serializer_text,beangle_doc_docx)
  ).dependsOn(core)

lazy val audit = (project in file("audit"))
  .settings(
    name := "openurp-std-graduation-audit",
    common,
    libraryDependencies ++= Seq(openurp_edu_grade_core)
  ).dependsOn(web)


lazy val archive = (project in file("archive"))
  .settings(
    name := "openurp-std-graduation-archive",
    common,
    libraryDependencies ++= Seq(openurp_edu_api)
  ).dependsOn(web)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin,TomcatPlugin)
  .settings(
    name := "openurp-std-graduation-webapp",
    common
  ).dependsOn(web,audit,archive)

publish / skip := true
