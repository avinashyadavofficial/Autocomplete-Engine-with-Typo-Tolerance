document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('autocomplete');
    const suggestionsList = document.getElementById('suggestions');
    const themeToggle = document.getElementById('theme-toggle');
    const sunIcon = document.getElementById('sun-icon');
    const moonIcon = document.getElementById('moon-icon');

    // Load saved theme from localStorage
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateIcons(currentTheme);

    input.addEventListener('input', async () => {
        const query = input.value;
        if (query.length > 0) {
            try {
                const response = await fetch(`/autocomplete?prefix=${encodeURIComponent(query)}`);
                const suggestions = await response.json();
                updateSuggestions(suggestions);
            } catch (error) {
                console.error('Error fetching autocomplete suggestions:', error);
            }
        } else {
            suggestionsList.innerHTML = '';
        }
    });

    function updateSuggestions(suggestions) {
        suggestionsList.innerHTML = suggestions.map(s => `<li>${s}</li>`).join('');
    }

    themeToggle.addEventListener('click', () => {
        const newTheme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateIcons(newTheme);
    });

    function updateIcons(theme) {
        if (theme === 'dark') {
            sunIcon.style.display = 'none';
            moonIcon.style.display = 'block';
        } else {
            sunIcon.style.display = 'block';
            moonIcon.style.display = 'none';
        }
    }
});
