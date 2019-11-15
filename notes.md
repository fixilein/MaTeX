# Note file structure: 

Document called 'title':
title
 | - title.md 
   ~~title.tex~~
 | - title.pdf 
~~|- title.config~~
 | - img
     | - image<timestamp>.jpg 
     | - image<timestamp>.jpg 

## .md file
stores contents of document in Markdown syntax. 

~~## .tex file 
LaTeX file converted by app
Loads packages (for math formuals, images, ...)and UTF encoding....~~

~~## .config file 
Stores properties of document created in GUI in App. 
Used to create appropriate LaTeX Header. (Stores Font, Page-margin, Table of contents, author, date)~~

## images
timestamp = time of inserting (every img unique)


# Activities, ...
## Main Activity 
List of saved documents sorted by last edited time (most recent on top)
Preview of document?? (first few lines?)
create new document -> title in dialog

### List item 
Title 
last edited time (in human readable format)

## Editor Activity 
auto save (save on change), if not performant enough: save button in menu bar
below action bar: "editor bar", like any other text editor (**bold**, *italic*, ...), is Android Fragment 
rest of activity: text field 
text formats (bold, ...** are previewed in text field

Preview right of the text editor (with bottom tabs)

## Config Activity 


# Md to ~~tex~~ PDF conversion 
ALL DONE ON PANDOC SERVER 
~~LaTeX Header is a standard header with alterations loaded from the config file~~
**text** to \textbf{text} *text* to \emph{text}
~~# Heading 1 (\n) to \section{Heading 1}~~
~~# Heading 2 (\n) to \subsection{Heading 2}~~

~~using regex~~
~~e.g:~~
~~input.replaceAll("<[^>]*>", "");~~
 
Server running pandoc 
send zip (.md, img folder) to server, get PDF back 

system architekur 
grafisch 
