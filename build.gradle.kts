plugins {
    java
}
group = "com.mahin"
version = "1.0"
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.alessiodp.com/releases/")
}
dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("net.citizensnpcs:citizens-main:2.0.43-SNAPSHOT")
}