load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "plato",
    srcs = glob(["*.java"]),
    visibility = [
        "//hobbes/src/main/java/com/sebster/weereld/hobbes:__pkg__",
        "//hobbes/src/test/java/com/sebster/weereld/hobbes/plugins/plato:__pkg__",
    ],
    deps = [
        "//3rdparty/org/projectlombok/lombok",
        "//hobbes/src/main/java/com/sebster/weereld/hobbes/plugins/api",
        "//hobbes/src/main/java/com/sebster/weereld/hobbes/plugins/plato/subscription",
        "//lib/telegram/botapi/src/main/java/com/sebster/telegram/botapi",
        "@maven//:commons_io_commons_io",
        "@maven//:org_apache_commons_commons_text",
        "@maven//:org_slf4j_slf4j_api",
        "@maven//:org_springframework_boot_spring_boot",
        "@maven//:org_springframework_spring_beans",
        "@maven//:org_springframework_spring_context",
    ],
)
