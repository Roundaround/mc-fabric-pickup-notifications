plugins {
  id("me.roundaround.allay")
}

repositories {
  mavenLocal()
}

allay {
  modrinth {
    dependencies {
      required("fabric-api")
    }
  }

  release {
    loaders.set(listOf("fabric", "quilt"))
  }
}

dependencies {
  libBundle(platform(libs.trove.bom))
  libBundle(libs.trove.fabric.core)
  libBundle(libs.trove.config.gui)
}
