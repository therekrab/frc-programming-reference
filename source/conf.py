from dataclasses import asdict

from sphinxawesome_theme import ThemeOptions
from sphinxawesome_theme.postprocess import Icons

# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'The Programming Reference Book'
copyright = '2025, Nolan Barker'
author = 'Nolan Barker'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = [
    "sphinx.ext.extlinks",
    "sphinx.ext.autodoc",
    "sphinx.ext.intersphinx",
    "hoverxref.extension",
]

hoverxref_roles = ["term"]
hoverxref_role_types = {"term": "tooltip"}


templates_path = ['_templates']
exclude_patterns = []


# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = 'sphinxawesome_theme'
html_title = project
html_permalinks_icon = Icons.permalinks_icon # nice icons
html_static_path = ['_static']

theme_options = ThemeOptions(
    awesome_external_links=True,
    show_breadcrumbs=True,
    breadcrumbs_separator=".",
    show_scrolltop=True,
)

html_theme_options = asdict(theme_options)
