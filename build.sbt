
scalaVersion := "2.13.8"

name := "scala-dev-mooc-2023-12"
organization := "ru.otus"
version := "1.0"

libraryDependencies += Dependencies.scalaTest
libraryDependencies ++= Dependencies.zio
libraryDependencies ++= Dependencies.zioConfig


scalacOptions += "-Ymacro-annotations"
scalacOptions += "-Xlint"
