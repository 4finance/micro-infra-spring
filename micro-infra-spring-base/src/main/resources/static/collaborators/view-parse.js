function parseResponse(response) {
    function nodeLinkDescriptor(address, collaboratorInstance, collaboratorStatus) {
        return {
            source: address,
            target: collaboratorInstance,
            type: statusToLabel(collaboratorStatus)
        };
    }

    function iterateOverCollaboratorsOfThisHost(collaborators, links, address) {
        iterateOverMap(collaborators, function (collaboratorName, collaboratorResponse) {
            iterateOverMap(collaboratorResponse, function (collaboratorInstance, collaboratorStatus) {
                links.push(nodeLinkDescriptor(address, collaboratorInstance, collaboratorStatus))
            });
        });
    }

    function nodeDescriptor(address, path, commonPrefix, hostStatus) {
        return {
            address: address.slice('http://'.length),
            path: path.slice(commonPrefix.length),
            status: statusToLabel(hostStatus.status)
        };
    }

    function iterateOverMap(map, entryCallbackFun) {
        for (key in map) {
            if (map.hasOwnProperty(key)) {
                entryCallbackFun(key, map[key]);
            }
        }
    }

    function statusToLabel(status) {
        return (status == 'UP') ? 'working' : 'broken';
    }

    function findCommonPrefix(strings) {
        if (strings.length < 2) {
            return '';
        }
        var firstChars = strings.map(function firstChar(arg) {
            return arg[0]
        });
        var allFirstCharsEqual = !!firstChars.reduce(function (a, b) {
            return (a === b) ? a : false
        });
        if (allFirstCharsEqual) {
            var withoutFirstChar = strings.map(function stripFirstChar(arg) {
                return arg.slice(1)
            });
            return strings[0][0] + findCommonPrefix(withoutFirstChar);
        } else {
            return '';
        }
    }

    //...

    var nodes = {};
    var links = [];
    var commonPrefix = findCommonPrefix(Object.keys(response));

    iterateOverMap(response, function (path, hostsStatus) {
        iterateOverMap(hostsStatus, function (address, hostStatus) {
            nodes[address] = nodeDescriptor(address, path, commonPrefix, hostStatus);
            var collaborators = hostStatus.collaborators;
            iterateOverCollaboratorsOfThisHost(collaborators, links, address);
        });
    });

    links.forEach(function (link) {
        link.source = nodes[link.source];
        link.target = nodes[link.target];
    });


    return {
        nodes: nodes,
        links: links
    }
}

