/* =========================================================================================
 * Copyright © 2013-2018 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */


val kamonCore       = "io.kamon" %% "kamon-core"              % "1.1.0"
val kamonTestkit    = "io.kamon" %% "kamon-testkit"           % "1.1.0"
val scalaExtension  = "io.kamon" %% "kanela-scala-extension"  % "0.0.11"

val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

lazy val root = (project in file("."))
  .enablePlugins(JavaAgent)
  .enablePlugins(JmhPlugin)
  .settings(name := "kamon-executors")
  .settings(scalaVersion := "2.12.2")
  .settings(crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.2"))
//  .settings(javaAgents += "io.kamon"  % "kanela-agent"  % "0.0.13"  % "compile;test")
  .settings(javaAgents += "io.kamon"  % "kanela-agent"  % "0.0.13"  % "compile")
  .settings(resolvers += Resolver.bintrayRepo("kamon-io", "snapshots"))
  .settings(resolvers += Resolver.mavenLocal)
  .settings(
      libraryDependencies ++=
      compileScope(kamonCore, logback, scalaExtension) ++
      testScope(scalatest, logbackClassic, kamonTestkit)
  )

