// This updater is used by standard-version to update our GlobalDependency file.
const versionRegex = /(    const val VERSION = ")([0-9]+\.[0-9]+\.[0-9]+)(")/g

module.exports.readVersion = function (contents) {
    return versionRegex.exec(contents)[2]
}

module.exports.writeVersion = function (contents, version) {
    return contents.replace(versionRegex, "$1" + version + "$3")
}