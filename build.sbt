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


val kamonCore       = "io.kamon" %% "kamon-core"              % "1.1.1-b8fbb33741cee85fa10ebc23a4d784a690c84da9"
val kamonTestkit    = "io.kamon" %% "kamon-testkit"           % "1.1.0"
val scalaExtension  = "io.kamon" %% "kanela-scala-extension"  % "0.0.10"
val kanealAgent = "io.kamon"  % "kanela-agent"  % "0.0.300"
val attacher = "io.kamon"  % "kanela-agent-attacher"  % "0.0.10"

val guava   = "com.google.guava"  % "guava"           % "24.1-jre"
val logback = "ch.qos.logback"    % "logback-classic" % "1.2.3"
val bytebuddyAgent = "net.bytebuddy" % "byte-buddy-agent" % "1.8.0"
val bytebuddy = "net.bytebuddy" % "byte-buddy" % "1.8.0"
val objectPool = "com.github.chrisvest" % "stormpot" % "2.4"


lazy val root = (project in file("."))
  .settings(noPublishing: _*)
  .aggregate(executors, benchmark)


val commonSettings = Seq(
  scalaVersion := "2.12.5",
  resolvers += Resolver.mavenLocal,
  crossScalaVersions := Seq("2.12.5", "2.11.12", "2.10.7"),
  scalacOptions ++= Seq("-opt:l:method")
)

lazy val executors = (project in file("kamon-executors"))
  .enablePlugins(JavaAgent)
  .settings(moduleName := "kamon-executors")
  .settings(commonSettings: _*)
  .settings(javaAgents += "io.kamon"  % "kanela-agent"  % "0.0.300"  % "compile;test")
  .settings(
    libraryDependencies ++=
      compileScope(kamonCore, logback, scalaExtension, bytebuddyAgent, bytebuddy, kanealAgent, attacher, objectPool) ++
      testScope(scalatest, logbackClassic, kamonTestkit, guava)
  )

lazy val benchmark = (project in file("kamon-executors-bench"))
  .enablePlugins(JmhPlugin)
  .settings(
    moduleName := "kamon-executors-bench",
    resolvers += Resolver.mavenLocal,
    fork in Test := true)
  .settings(noPublishing: _*)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= compileScope(guava))
  .dependsOn(executors)