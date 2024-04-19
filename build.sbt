import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*
import sbt.Keys.libraryDependencies

ThisBuild / organization := "org.openurp.std.graduation"
ThisBuild / version := "0.0.3-SNAPSHOT"

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

val apiVer = "0.38.1-SNAPSHOT"
val starterVer = "0.3.30"
val baseVer = "0.4.22"
val eduCoreVer = "0.2.2-SNAPSHOT"
val stdCoreVer = "0.0.1-SNAPSHOT"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_std_core = "org.openurp.std" % "openurp-std-core" % stdCoreVer


val ojdbc11 = "com.oracle.database.jdbc" % "ojdbc11" % "23.3.0.23.09"
val orai18n = "com.oracle.database.nls" % "orai18n" % "23.3.0.23.09"

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-std-graduation-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api, beangle_ems_app),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag),
    libraryDependencies ++= Seq(openurp_edu_core,openurp_std_core),
    libraryDependencies ++= Seq(ojdbc11,orai18n),
    libraryDependencies ++= Seq(beangle_doc_docx)
  )

