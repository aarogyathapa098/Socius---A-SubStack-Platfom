---
name: Socius Civic Editorial
colors:
  surface: '#fff8f7'
  surface-dim: '#ecd5d3'
  surface-bright: '#fff8f7'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#fff0ef'
  surface-container: '#ffe9e7'
  surface-container-high: '#fbe3e1'
  surface-container-highest: '#f5dddb'
  on-surface: '#251818'
  on-surface-variant: '#58413f'
  inverse-surface: '#3b2d2c'
  inverse-on-surface: '#ffedeb'
  outline: '#8c716e'
  outline-variant: '#e0bfbc'
  surface-tint: '#ad3031'
  primary: '#7a0412'
  on-primary: '#ffffff'
  primary-container: '#9b2226'
  on-primary-container: '#ffb0ab'
  inverse-primary: '#ffb3ae'
  secondary: '#5f5e5e'
  on-secondary: '#ffffff'
  secondary-container: '#e2dfde'
  on-secondary-container: '#636262'
  tertiary: '#00404e'
  on-tertiary: '#ffffff'
  tertiary-container: '#00596b'
  on-tertiary-container: '#8ccee3'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdad7'
  primary-fixed-dim: '#ffb3ae'
  on-primary-fixed: '#410005'
  on-primary-fixed-variant: '#8c161d'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c8c6c5'
  on-secondary-fixed: '#1b1b1c'
  on-secondary-fixed-variant: '#474746'
  tertiary-fixed: '#b1ecff'
  tertiary-fixed-dim: '#8ed0e5'
  on-tertiary-fixed: '#001f27'
  on-tertiary-fixed-variant: '#004e5e'
  background: '#fff8f7'
  on-background: '#251818'
  surface-variant: '#f5dddb'
typography:
  display-xl:
    fontFamily: Newsreader
    fontSize: 48px
    fontWeight: '600'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Newsreader
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.2'
  headline-md:
    fontFamily: Newsreader
    fontSize: 24px
    fontWeight: '500'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  label-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: '1.2'
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1120px
  gutter: 24px
  margin-edge: 32px
  stack-sm: 12px
  stack-md: 24px
  stack-lg: 48px
---

## Brand & Style

The design system is rooted in the principles of **Premium Editorial Minimalism**. It moves away from the frenetic energy of typical social platforms, favoring the steady, authoritative cadence of a high-end digital publication. The brand personality is civic-minded, intellectual, and composed, designed to foster deep engagement rather than impulsive scrolling.

The aesthetic relies on "active negative space," where whitespace is treated as a structural element that guides the eye. It combines the prestige of traditional print journalism with the functional efficiency of modern SaaS. By utilizing a restrained color palette and a disciplined typographic hierarchy, the design system establishes an environment of trust and clarity essential for community governance and discourse.

## Colors

The color strategy for the design system is intentional and high-contrast, designed to maximize legibility. 

- **Foundation:** The background uses a warm, bone-white neutral (#FDFCFB) to reduce eye strain and provide a more "organic" feel than pure white.
- **Typography:** The primary ink color is a deep charcoal (#1F1F1F), providing maximum contrast while remaining softer than true black.
- **Accents:** A deep, scholarly red (#9B2226) is used sparingly for primary actions, critical alerts, and brand signifiers.
- **Semantic Logic:** Moderation states use desaturated, "earth-tone" variants of green and gold to maintain the editorial calm, ensuring that even system alerts do not feel "noisy."

## Typography

This design system employs a classic serif-on-sans pairing to establish a clear information hierarchy. 

**Newsreader** is the voice of the platform. Used for headlines and long-form titles, its traditional and authoritative letterforms evoke the feeling of a respected broadsheet. It should be typeset with slightly tighter tracking in larger sizes to maintain a cohesive visual "block."

**Inter** provides the functional engine. Used for body text, interface labels, and data, its neutral and utilitarian nature ensures that complex community discussions remain highly readable across all devices. Body text should maintain a generous line height (1.5x to 1.6x) to facilitate long-form reading without fatigue.

## Layout & Spacing

The design system utilizes a **Fixed Grid** model centered on a 1120px max-width container for desktop layouts. This constraint mimics the column width of an editorial layout, preventing line lengths from becoming too wide for comfortable reading.

The spacing rhythm is strictly based on an 8px scale. 
- **Vertical Rhythm:** Use larger gaps (stack-lg) between distinct content sections to allow the design to "breathe."
- **Grouped Elements:** Use tighter gaps (stack-sm) for related items like a label and its corresponding input field.
- **Margins:** Generous page margins ensure that content never feels cramped against the viewport edges, reinforcing the premium aesthetic.

## Elevation & Depth

Depth in this design system is achieved through **Low-contrast outlines** and tonal layering rather than aggressive shadows. This keeps the interface feeling flat, organized, and "printed."

- **Borders:** Use thin (1px) borders in a muted version of the text color (opacity 10-15%) to define containers and cards.
- **Shadows:** When necessary to indicate interactivity (like a hovering card), use a single, highly-diffused "ambient" shadow. The shadow should have a large blur radius (16px+) and very low opacity (5%), appearing more like a soft glow than a physical drop shadow.
- **Surface Tiers:** Use subtle shifts in background color (e.g., a slightly cooler or warmer neutral) to distinguish between a global navigation bar and the main content area.

## Shapes

The design system uses a **Rounded** shape language to soften the serious nature of the typography and civic subject matter. 

- **Standard Radius:** 0.5rem (8px) is applied to all primary components, including buttons, input fields, and cards.
- **Large Radius:** 1rem (16px) is used for decorative containers or image masks.
- **Pill Shapes:** Reserved exclusively for tags and status badges (like moderation status) to distinguish them from actionable buttons.

This consistent rounding ensures the platform feels modern and accessible, preventing the "institutional" stiffness often found in civic software.

## Components

### Buttons
Primary buttons use the deep red accent (#9B2226) with white text. Secondary buttons use a charcoal outline with no fill. All buttons should have a 0.5rem corner radius and generous horizontal padding (at least 2x the vertical padding).

### Moderation Status Badges
Badges are pill-shaped with a light background tint and a darker text color of the same hue:
- **Pending:** Soft gold background, amber text.
- **Approved:** Pale sage background, deep forest text.
- **Rejected:** Faint rose background, deep red text.

### Form Fields
Grouped for clarity. The label sits above the field in `label-sm` typography. Hints or validation messages sit below the field in a smaller sans-serif size. Fields use a thin charcoal border that thickens slightly on focus.

### Editorial Cards
Cards are the primary container for feed items. They feature a thin border, no default shadow, and significant internal padding (24px or 32px). The headline within the card always uses the serif `newsreader` font.

### Civic Lists
For lists of members or rules, use a "clean line" approach: no containers, just 1px horizontal dividers between items with ample vertical padding to ensure each list item feels distinct and readable.