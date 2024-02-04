module.exports = {
    branches: ["main"],
    tagFormat: "${version}",
    plugins: [
        "@semantic-release/commit-analyzer",
        "@semantic-release/release-notes-generator",
        "@semantic-release/changelog",
        [
            "@semantic-release/exec",
            {
                prepareCmd:
                    "gradle cleanAll clean build bootJar -x test -Pversion=${nextRelease.version} > /dev/null",
            },
        ],
        [
            "@semantic-release/git",
            {
                assets: ["CHANGELOG.md"],
                message:
                    "chore(release): ${nextRelease.version} [skip ci]\n\n${nextRelease.notes}",
            },
        ],
        [
            "@semantic-release/github",
            {
                assets: [
                    {
                        path: "build/libs/minerva-boot-*.jar",
                        label: "bootJarX86_64",
                    },
                ],
            },
        ],
    ],
};
