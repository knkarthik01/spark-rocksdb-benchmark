name := "spark-benchmark"

version := "0.1"

scalaVersion := "2.12.18"

resolvers += Resolver.mavenLocal
resolvers ++= List("Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository")

// scallop is MIT licensed
libraryDependencies += "org.rogach" %% "scallop" % "3.1.2"

val sparkVersion = "3.4.0"
val scopeForSparkArtifacts = "provided"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.12" % sparkVersion % scopeForSparkArtifacts
libraryDependencies += "org.apache.spark" % "spark-sql_2.12" % sparkVersion % scopeForSparkArtifacts
libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion excludeAll(
  ExclusionRule(organization = "org.spark-project.spark", name = "unused"),
  ExclusionRule(organization = "org.apache.spark", name = "spark-streaming"),
  ExclusionRule(organization = "org.apache.kafka", name = "kafka-clients"),
  ExclusionRule(organization = "org.apache.hadoop")
)
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0" excludeAll(
  ExclusionRule(organization = "net.jpountz.lz4", name = "lz4")
  )

target in assembly := file("build")

/* including scala bloats your assembly jar unnecessarily, and may interfere with
   spark runtime */
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyJarName in assembly := s"${name.value}.jar"
