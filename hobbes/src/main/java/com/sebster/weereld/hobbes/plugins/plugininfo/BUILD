load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "plugininfo",
    srcs = glob(["*.java"]),
    visibility = ["//hobbes/src/main/java/com/sebster/weereld/hobbes:__pkg__"],
    deps = [
        "//3rdparty/org/projectlombok/lombok",
        "//hobbes/src/main/java/com/sebster/weereld/hobbes/plugins/api",
        "//lib/telegram/botapi",
        "@maven//:org_springframework_spring_context",
    ],
)
