#!/bin/bash

set -e

function install_lein {
    echo "Installing lein..."
    mkdir -p $(dirname ${LEIN})
    curl -o ${LEIN} https://raw.github.com/technomancy/leiningen/preview/bin/lein
    chmod +x ${LEIN}
}

function lein {
    export LEIN="${HOME}/bin/lein"
    [ -x ${LEIN} ] || install_lein
    ${LEIN} do $@
}

lein clean, check, midje, jar, doc
