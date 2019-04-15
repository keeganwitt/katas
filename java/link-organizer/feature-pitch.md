# Easy contact sharing (e.g. digital business cards)

## Introduction

I've felt there was a need for an app like [Bump](http://bu.mp/), now that Google
[acquired](http://blog.bu.mp/post/61411611006/bump-google) it and killed it off. It seems bizarre to me that we still
manually dial and call someone to share each others contact information. It feels so primitive.

Multiple [patents](https://patents.google.com/?assignee=BUMP+TECHNOLOGIES%2c+INC.) were acquired by Google when it acquired
Bump, Inc. Some that are likely relevant to any alternative implementation are
* [US8577292B2](https://patents.google.com/patent/US8577292B2/)
* [US20160337161A1](https://patents.google.com/patent/US20160337161A1/)
* [US9607024B2](https://patents.google.com/patent/US9607024B2/)

## Technical implementation options

It was my design goal to not have to pay for a server (because I'm cheap), but also because it avoids any privacy issues
associated with storing user's data.

### NFC

One option which I believe avoids patent violations would be to transfer the information directly using NFC,
and avoid using a server entirely. One major problem with this approach is while it's viable for Android, iOS limits access
to NFC to a few limited services (like Apply Pay). Any solution that doesn't allow sharing regardless of what device the user
chose is a non-starter. This also wouldn't allow users to share data unless both had the app installed. This option I rejected.

### QR codes

Another option I believe avoids the patent violations is to create QR codes on-demand to store vCard information. This
would work by entering your information into the app. This will be stored in the app's local storage (possibly letting
you sync to Dropbox, Google Drive, etc). When the user elects to share, they can select what information to share (e.g.
share email, but not phone number), and a vCard is generated, and transformed into a QR code.

The app will require access to the iOS/Android contacts and camera APIS. If we wanted to allow usage with fewer permissions,
we could later let users user their own app to scan the QR code (e.g. Google Lens). This would also have the advantage
that data could be shared, even if the receiving user didn't have the app installed. We could also add an import from
picture option so the user could take a picture with their camera app, then install the app and import the contact later.

#### Challenges

One downside of this approach is that unlike the ease of use Bump provided, two pictures have to be taken for this to work.
But the only other option I can think of involves servers and would require research into whether it'd violate current patents.
Much of the patents Bump held were around synchronizing requests to a server to share between two clients.

Another problem is there are fields not yet incorporated into the vCard standard. For example, social networks. We will
have to extend the spec. For example, Apple has their own vCard variant that does this.

## User experience

### Home screen

Has 3 buttons on it: "share", "edit contact info", and "settings". If/when multiple contact profiles are supported the
"edit contact info" button will need renamed.

### Share screen

Share screen is navigated to from the "share" button on the home screen. You are presented with a set of toggle buttons,
which allow selection of what data should be shared. There's also a big green right arrow button that displays QR code
once selection is complete.

### QR code screen

QR code screen is navigated to from the big green right arrow on the share screen. At the top of the screen there's a
back button to navigate back to the share screen.

### Edit contact info screen

Edit contact info screen is navigated to from the "Edit contact info" button on home screen. It has entry boxes for all
vCard fields, which allows you to create your contact info, as well as a button next to each field to remove the field
entirely. Some fields will be mandatory (minimally those required by the vCard spec, which includes name).

### Settings screen

Settings screen is navigated to from the "Settings" butt on the home sreen. It has a section for controlling what data
fields should be selected to share by default.

## Potential revenue models

1. Ad-sponsored
1. A "pro" version
    1. Limited number of times per month your contact info can be shared without upgrading to "pro"
    1. Certain vCard fields (business oriented fields) can only be used with "pro"
        * [title](https://tools.ietf.org/html/rfc6350#section-6.6.1)
        * [logo](https://tools.ietf.org/html/rfc6350#section-6.6.3)
        * [org](https://tools.ietf.org/html/rfc6350#section-6.6.4)
    1. Ability to have multiple identities for sharing
1. Time based trial

I feel teh pro version is probably best here, as it allows free personal use, but professionals will likely want the
paid features. I'm not completely certain the price point, but given the recurring expenses are quite low, I think the
price point could be also very low while still maintaining a good margin.
