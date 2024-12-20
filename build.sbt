import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*
import sbt.Keys.libraryDependencies

ThisBuild / organization := "org.openurp.std.graduation"
ThisBuild / version := "0.0.9-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-graduation"),
    "scm:git@github.com:openurp/std-graduation.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std graduation"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-graduation/index.html"))

val apiVer = "0.41.14"
val starterVer = "0.3.48"
val baseVer = "0.4.46"
val eduCoreVer = "0.3.7"
val stdCoreVer = "0.0.16"

val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_std_core = "org.openurp.std" % "openurp-std-core" % stdCoreVer

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-std-graduation-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api, beangle_ems_app),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag),
    libraryDependencies ++= Seq(openurp_edu_core, openurp_std_core),
    libraryDependencies ++= Seq(beangle_doc_docx)
  )

