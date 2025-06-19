import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*
import sbt.Keys.libraryDependencies

ThisBuild / organization := "org.openurp.std.graduation"
ThisBuild / version := "0.0.11"

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

val apiVer = "0.44.0"
val starterVer = "0.3.58"
val baseVer = "0.4.51"
val eduCoreVer = "0.3.11"
val stdCoreVer = "0.0.20"

val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_std_core = "org.openurp.std" % "openurp-std-core" % stdCoreVer

val beangle_doc_excel = "org.beangle.doc" % "beangle-doc-excel" % "0.4.14"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-std-graduation-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api, beangle_ems_app),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag),
    libraryDependencies ++= Seq(openurp_edu_core, openurp_std_core),
    libraryDependencies ++= Seq(beangle_doc_docx, beangle_doc_excel)
  )

