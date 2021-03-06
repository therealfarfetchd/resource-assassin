@file:Suppress("PropertyName")

import net.minecraftforge.gradle.user.UserBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mod_version: String by extra
val mc_version: String by extra
val forge_version: String by extra
val mappings_version: String by extra
val kotlin_version: String by extra
val jei_version: String by extra
val forgelin_version: String by extra

val Project.minecraft: UserBaseExtension
  get() = extensions.getByName<UserBaseExtension>("minecraft")

buildscript {
  val kotlin_version: String by extra
  repositories {
    jcenter()
    mavenCentral()
    maven("http://files.minecraftforge.net/maven")
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    classpath(kotlin("gradle-plugin", kotlin_version))
  }
}

plugins {
  java
}

apply {
  plugin("net.minecraftforge.gradle.forge")
  plugin("kotlin")
}

version = mod_version
group = "therealfarfetchd.resourceassassin"

minecraft {
  version = "$mc_version-$forge_version"
  runDir = "run"
  mappings = mappings_version
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

repositories {
  mavenCentral()
  maven("http://maven.shadowfacts.net/")
}

dependencies {
  compile(kotlin("stdlib-jdk8", kotlin_version))
  compile(kotlin("reflect", kotlin_version))

  runtimeOnly("net.shadowfacts", "Forgelin", forgelin_version)
}

tasks.withType<Jar> {
  inputs.properties += "version" to project.version
  inputs.properties += "mcversion" to project.minecraft.version

  baseName = project.name

  filesMatching("/mcmod.info") {
    expand(mapOf(
      "version" to project.version,
      "mcversion" to project.minecraft.version
    ))
  }

  manifest {
    attributes(mapOf(
      "FMLCorePlugin" to "therealfarfetchd.resourceassassin.hax.ResourceAssassinPlugin",
      "FMLCorePluginContainsFMLMod" to "true",
      "Maven-Artifact" to getMavenArtifactId(),
      "Timestamp" to System.currentTimeMillis()
    ))
  }
}

fun getMavenArtifactId(): String {
  var version = project.version.toString()
  if (System.getenv("BUILD_NUMBER") != null && System.getenv("SHOW_BUILD_NUMBER") != null)
    version += "_" + System.getenv("BUILD_NUMBER")
  return "$group:$name:$version"
}

fun DependencyHandler.deobfCompile(
  group: String,
  name: String,
  version: String? = null,
  configuration: String? = null,
  classifier: String? = null,
  ext: String? = null): ExternalModuleDependency =
  create(group, name, version, configuration, classifier, ext).apply { add("deobfCompile", this) }

fun DependencyHandler.deobfCompile(dependencyNotation: Any): Dependency? =
  add("deobfCompile", dependencyNotation)

fun minecraft(op: UserBaseExtension.() -> Unit) = configure(op)