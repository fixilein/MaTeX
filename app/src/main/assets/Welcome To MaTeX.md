# Introduction
MaTeX is an app in which you create LaTeX Documents using Markdown syntax with ease.

> Tip: you can restore this guide at any time, from the documents-list.

# Writing Documents in MaTeX

## Basic Text
Bold text is written **like this**. Italic *like this*.
Of course you don't have to remember all that, just use the formatting buttons when ever you want.

Creating a new paragraph requires an empty newline.
Similarly, in order to LaTeX often expects a newline before syntax like lists and quotes.
If something does not convert to LaTeX properly, try adding a newline before!.

## Heading
Headings start with a number sign ('#'). The more of number sings the more nested is the heading (up to a maximum of six).
You can easily add and remove heading levels using the buttons '**H+**' and '**H-**'.

## Lists
### Numbered Lists
Numbered Lists look like this:

1. Eggs
2. Milk
3. Beer

MaTeX will automatically add the correct number if you press enter on the last entry. Try it!

Tip: if the list (ordered or unordered is displaying incorrectly, make sure there is an empty line above the list.

### Unordered Lists
Unordered Lists look like this:

- I have
- a lot
- of things
- to do.

## Images
Insert images by using the image button. MaTeX will ask you to import an image and give it a name.

The text in the first brackets is the description that will display below the image in the PDF.

## Links
Inserting links is similar to inserting images; use the button. If there is a link already in your clipboard, MaTeX will ask you if you want to use it.
Otherwise you can manually enter the URL Address.
The text in the first brackets is th URL text which will be displayed as the link.

> Tip: There is a setting in the Config Tab, which allows links to be colored or not.

## Quotes
Quotes look like this.

> Don't worry. Be happy.

## Footnotes
Footnotes can be inserted using the following syntax[^1]. This marks the position of the footnote.

[^1]: This is the content of the footnote.


## Code
Code can be written like this:
```
class Main {
    public static void main(String args[]) {
        System.out.println("Hello World");
    }
}
```

## Math Formulas
You can write normal \LaTeX formulas in Markdown using the following syntax (or the buttons).

$$
\alpha = \sqrt{\frac{3}{n^t} \times \pi }
$$

There is also an option for in-line math: $x=\frac{3.14}{7}$

## Latex Syntax
If you want to use \LaTeX Syntax just as you are used to. A good example for that would be the pagebreak;
use it to ... well break the page.

\pagebreak

See what I mean?

## Getting help
If you need syntax specific help for writing Documents in MaTeX, search online for *'Pandoc Markdown Latex'*.
Pandoc is the program which converts the markdown file to a LaTeX PDF.

# Sharing and Exporting Your Document
In the top of the app, you can share/export your pdf in three ways: The PDF you generated (the last version you viewed in the PDF Tab will be used),
the Markdown (.md) file, or a ZIP file with the Markdown file and all the images you added to the document.


# Configuration
In the configuration Tab are options to control how the produced PDF should look.
They should be self explanatory, so they will not be explained in detail here.


