Cypress.Commands.add("storeText", { prevSubject: true }, (subject, key) => {
    store(key, subject.invoke('text'));
});
