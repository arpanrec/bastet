module.exports = {
    branches: ["main"],
    tagFormat: "${version}",
    plugins: [
        [
            "@semantic-release/commit-analyzer",
            {
                preset: "angular",
                parserOpts: {
                    noteKeywords: [
                        "BREAKING CHANGE",
                        "BREAKING CHANGES",
                        "BREAKING",
                    ],
                },
            },
        ],
        [
            "@semantic-release/release-notes-generator",
            {
                preset: "angular",
                parserOpts: {
                    noteKeywords: [
                        "BREAKING CHANGE",
                        "BREAKING CHANGES",
                        "BREAKING",
                    ],
                },
                writerOpts: {
                    commitsSort: ["subject", "scope"],
                },
            },
        ],
        [
            "@semantic-release/changelog",
            {
                changelogFile: "CHANGELOG.md",
            },
        ],
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
