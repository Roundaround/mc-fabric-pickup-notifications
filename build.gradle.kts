plugins {
  id("me.roundaround.allay")
}

allay {
  displayName.set("Pickup Notifications")
  description.set("Show notifications on item pickup.")
  authors.set(listOf("Roundaround"))
  license.set("MIT")
  homepage.set("https://modrinth.com/mod/pickup-notifications")
  repository.set("https://github.com/Roundaround/mc-fabric-pickup-notifications")
  issues.set("https://github.com/Roundaround/mc-fabric-pickup-notifications/issues")
  logoFile.set("assets/pickupnotifications/banner.png")

  gametest {
    // Acknowledge the Minecraft EULA for the throwaway worlds the headless
    // server game test spins up.
    eula.set(true)
  }

  modrinth {
    projectId.set("pickup-notifications")
  }

  curseforge {
    projectId.set(1292780)
  }

  release {
    versionType.set("release")
    minecraftVersions("26.3")
    changelogDir.set(file("changelogs"))
  }
}
