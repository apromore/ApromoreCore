
const copyToClipboard = (string) => {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(string);
    } else {
        console.error("Copying to clipboard not supported.")
    }
};