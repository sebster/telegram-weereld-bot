load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "mem",
    srcs = glob(["*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        "//3rdparty/org/projectlombok/lombok",
        "//lib/repository/api",
    ],
)
