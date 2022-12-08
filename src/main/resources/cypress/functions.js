const DEFAULT_TIMEOUT = 15000;

function createEmptyState(specName) {
    return {
        blurred: [],
        variables: {},
        specName: specName
    }
}

function translateQuery(query) {
    if (query.includes("@")) {
        const [prefix, suffix, ..._] = query.split('@');

        return `${prefix}:contains('${suffix}')`;
    }

    return query;
}

function open(url) {
    cy.visit(url, { timeout: DEFAULT_TIMEOUT });
}

function size(width, height, zoom) {
    zoom = zoom ?? 1;
    cy.viewport(width * zoom, height * zoom);
}

function blur(elements, options) {
    elements = Array.isArray(elements) ? elements : [elements];

    for (const element of elements) {
        cy
            .get(translateQuery(element), { options: DEFAULT_TIMEOUT })
            .invoke('css', 'filter', 'blur(4px)');

        STATE.blurred.push(element);
    }
}

function click(elements, options) {
    elements = Array.isArray(elements) ? elements : [elements];

    for (const element of elements) {
        cy.get(translateQuery(element), { timeout: DEFAULT_TIMEOUT }).click();
    }
}

function unblur(elements) {
    elements = !elements ? STATE.blurred : (Array.isArray(elements) ? elements : [elements]);

    for (const element of elements) {
        cy.log(translateQuery(element));
        cy
            .get(translateQuery(element), { options: DEFAULT_TIMEOUT })
            .invoke('css', 'filter', '');

        STATE.blurred = STATE.blurred.filter(e => e !== element);
    }
}

function screenshot(name, element) {
    cy.get(translateQuery(element)).screenshot(
        name,
        {
                overwrite: true,
                disableTimersAndAnimations: true
        }
    );
}

function type(element, value) {
    cy.get(translateQuery(element)).type(value);
}

function store(key, fn) {
    fn.then(value => {
        STATE.variables[key] = value;
        cy.log(`Set ${key} = ${JSON.stringify(value, null, 2)}`);
    });
}

function __propertyMapper(elements, fn) {
    const htmlElements = elements.toArray();

    if (htmlElements.length === 0) {
        return null;
    } else if (htmlElements.length === 1) {
        return fn(htmlElements[0]);
    }

    return htmlElements.map(fn);
}

function text(element) {
    return cy.get(translateQuery(element)).then(
        elements => __propertyMapper(elements, element => element.innerText)
    );
}

function value(element) {
    return cy.get(translateQuery(element)).then(
        elements => __propertyMapper(elements, element => element.value)
    );
}

function storeProperties() {
    cy.writeFile(`properties/${STATE.specName}.json`, STATE.variables);
}

afterEach(() => {
    storeProperties()
});

let STATE = createEmptyState("");
