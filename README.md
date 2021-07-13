Original App Design Project - README Template
===

# Platform (temporary name)

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description

Platform creates a community for users to share their thoughts about the TV shows and movies they have playing on their screens without having to leave the comfort of their room. With comment threads operating in real-time, users are guranteed the opportunity to discuss what happened just moments ago. An unexpected plot twist, the death of the character everyone fell in love with, and even theories about what will happen after the out-of-nowhere cliffhanger -- this is the platform to share it all and from other users as well. Furthermore, users are given a continuous feed of trending titles based upon their filter-selection. This feed is great not only for those times when users just want something new to watch, but also helpful for other users. With the up and down-voting feature for each title, which include the option to provided keywords to describe the title, other users are guranteed a more accurate viewing of trending titles.


### App Evaluation
- **Category:** Social / Entertainment
- **Mobile:** Mobile makes it a convience to engage with fellow users and view trending material without the nuisance of dealing with a desktop. App actions (i.e voting and chating) become easier while push-notifications keep user up-to-date regarding titles, communities, and more. 
- **Story:** After watching something, whether a short clip or a 2-hour movie, most people have some thoughts and questions about it. To address this, certain platforms (i.e YouTube) have a comment section. However, for many entertainment platforms (Netflix, Hulu, etc.), there is no such thing, forcing many viewers to keep these thoughts and questions to themself or scrolling online. Platform changes all of that as it gives user a space to share their thought, ask questions, and simply engage with one another.
- **Market:** As the purpose of the app is to act as a community for media watchers, the number of users potentially runs large in relation to that audience size. Wouldn't say there is a huge value, but the app does carry worth for the abundance of viewers who would like a clearly dedicated comment section for their TV shows and movies.
- **Habit:** Considering that this app is a companian to media, it could be opened many times throughout the day by a single user. That said, a more 'reasonable' expection could 3-5 during a week. The average user has the opportunity to both consume and create content on the app, thus making it more engageable and possibly increasing how often they open the app.
- **Scope:** The app will be just the right level of challenging for the most foundational features of the app (commenting, rating, etc.). Adding additional features could possible pose a challenging depending on the feature and how I want to implement it (i.e sorting algorithms). That said, the stripped-down version of the app which includes the foundational features would still be worth building.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

User can...

- Create a new account
- Login & Logout
- Scroll through trending feed (TV shows and movies)
- View a detailed screen of the title
- View a detail screen of each episode for a TV show title
- Gesture on home feed to...
- Animation of each Title cover on home feed
- Upvote and downvote each trend, potentially changing its position
- Search for titles
- View results for a search
- Follow and comment on each title
- Comment on these individual episode
- Reply to all comments
- View the most common words other users use to describe a title or episode
- View their profile screen
- Place phone in landscape mode 

**Optional Nice-to-have Stories**

User can...
- Voting prompts user to chose keywords to describe title
- Sensor comments with a 'spoiler' tag
- Search through a catalog of titles organized by categories: TV shows, Movies, Platform, and Genre
- Be notified about new comments for a title they are following
- Chat directly with other users
- View the profiles of other users
- Create and view communities
- View their app settings


### 2. Screen Archetypes

* Login Screen
    * User can login or create new account
* Registration Screen
    * User can create a new account
* Stream Screen
    * User can scroll through trending feed (TV shows and movies)
    * User can search for titles
    * User can upvote and downvote each trend, potentially changing its position, which prompts user to chose keywords to describe title
* Search Screen
    * User can make a search and view results
* Title Detail Screen
    * User can view a detailed screen of the title
    * User can follow and comment on each title
    * User can reply to comments made by other users
* Episode Detail Screen
    * User can view a detail screen of each episode for a TV show title
    * User can comment on these individual episode
    * User can reply to comments made by other users

### 3. Navigation

**Tab Navigation** (Tab to Screen, Going left to right)

* Communities
* Catalog
* Home
* Chats
* Inbox

**Flow Navigation** (Screen to Screen)

* Login Screen
    * => Registration Screen
    * => Trending Feed Screen
* Registration Screen
    * => Trending Feed Screen
* Stream Screen
    * => Title Detail Screen
    * => Search Screen
* Search Screen
    * => Title Detail Screen
* Title Detail Screen
    * => Episode Detail Screen (if title is a TV show)
* Episode Detail Screen
    * => Nowhere

## Digital Wireframes

## Schemas
### Models

User
| Property     | Type             | Description                                      |
| ------------ | ---------------- | ------------------------------------------------ |
| objectID     | String           | unique id for the user                           |
| createdAt    | Date             | creation data of the user account                |
| fullName     | String           | last and first name of the user                  |
| username     | String           | chosen screen name of the user                   |
| email        | String           | email address for the user account               |
| password     | String           | password for user to log into their account      |
| titleLikes   | List<Title/>     | the titles and episodes a user has liked         |
| commentLikes | List<Comment/>   | the comments a user has liked                    |
| following    | List<Title/>     | titles and episodes a user is following          |
| chats        | List<Chat/>      | the chat messages the user has with another user |
| communities  | List<Community/> | communities the user is a part of                |

Title
| Property         | Type            | Description                                  |
| ---------------- | --------------- | -------------------------------------------- |
| objectID         | String          | unique id for the title                      |
| name             | String          | name of the title                            |
| coverPath        | String          | path to cover for the title                  |
| type             | String          | if the title is a TV show, movie, or episode |
| description      | String          | what the title is about                      |
| genres           | List<Genre/>    | genres the title fits into                   |
| starring         | List<Actor/>    | popular actors that are in the title         |
| releaseDate      | Date            | when the title was released                  |
| availableOn      | List<Provider/> | where the title can be watched               |
| likes            | int             | number of likes for a title                  |
| comments         | List<Comment/>  | the comments made for a title by users       |
| shares           | int             | number of shares for a title                 |
| seasons          | List<Season/>   | the seasons associated with a title          |
| numberOfEpisodes | int             | total number of episodes for the title       |

Genre
| Property | Type   | Description                               |
| -------- | ------ | ----------------------------------------- |
| objectID | String | unique id for the genre                   |
| tmdbID   | int    | unique id for the genre according to TMDb |
| name | String | the name of the genre |
| tmdbID | int | unique id for the genre according to TMDb |


Actor
| Property | Type   | Description                               |
| -------- | ------ | ----------------------------------------- |
| objectID | String | unique id for the actor                   |
| tmdbID   | int    | unique id for the actor according to TMDb |
| fullName | String | name of the actor                         |

Provider
| Property | Type   | Description                                                |
| -------- | ------ | ---------------------------------------------------------- |
| objectID | String | unique id for the provider                                 |
| tmdbID   | int    | unique id for the provider according to TMDb               |
| name     | String | name of the provider                                       |
| logoPath | String | path to the image of the logo associated with the provider |

Season
| Property | Type           | Description                                |
| -------- | -------------- | ------------------------------------------ |
| objectID | String         | unique id for the season                   |
| tmdbID   | int            | unique id for the season according to TMDb |
| episodes | List<Episode/> | the episodes contained within the season   |

Episode
| Property      | Type   | Description                                 |
| ------------- | ------ | ------------------------------------------- |
| objectID      | String | unique id for the title                     |
| tmdbID        | int    | unique id for the episode according to TMDb |
| name          | int    | name of the episode                         |
| stillPath     | String | path to the episode image                   |
| description   | String | an overview of the episode                  |
| seasonNumber  | int    | number of the season the episode is in      |
| episodeNumber | int    | number of episode within the season         |

Comment
| Property  | Type            | Description                           |
| --------- | --------------- | ------------------------------------- |
| objectID  | String          | unique id for the comment             |
| createdAt | Date            | when the comment was created          |
| User      | Pointer to User | user who wrote and posted the comment |
| text      | String          | the comment made by the user          |
| likes     | int             | number of likes for the comment       |
| replies   | List<Comment/>  | replies to the comment                |

Chat
| Property      | Type            | Description                                        |
| ------------- | --------------- | -------------------------------------------------- |
| objectID      | String          | unique id for the chat                             |
| receivingUser | Pointer to User | who is receiving messages from the sender          |
| updatedAt     | Date            | the last time a message was sent between the users |
| read          | boolean         | whether the user has opened the chat or not        |
| messages      | List<Message/>  | the messages sent within the chat                  |

Message
| Property  | Type            | Description                   |
| --------- | --------------- | ----------------------------- |
| objectID  | String          | unique id for the message     |
| createdAt | Date            | when the message was created  |
| sender    | Pointer to User | user who sent the message     |
| receiver  | Pointer to User | user who received the message |
| content   | String          | the content of the message    |

Community
| Property    | Type            | Description                               |
| ----------- | --------------- | ----------------------------------------- |
| objectID    | String          | unique id for the community               |
| createdAt   | Date            | when the community was created            |
| owner       | Pointer to User | the user who created the community        |
| description | String          | description of the community by its owner |
| members     | List<User/>     | users who are part of the community       |
| messages    | List<Message/>  | messages between users in the community   |

## Credits
- TMDb API
- Email Verification API
- Words API (Dictionary)
- Parse SDK
- Back4App
- Async Client
- External libraries
