<img width="806" height="404" alt="image" src="https://github.com/user-attachments/assets/9b77f1d7-13b3-47bc-b56d-4eacb3cfa919" />
<img width="739" height="573" alt="image" src="https://github.com/user-attachments/assets/3ea19861-dc2b-4a08-abe3-047eb494c108" />
<img width="766" height="573" alt="image" src="https://github.com/user-attachments/assets/8cc20074-d966-49d9-b675-4d98ee8f91f9" />





Inspiration
Environmental harm is often noticed by local communities before it is formally recorded, yet observations such as water discoloration, unusual odors or fish mortality rarely become structured evidence. We selected the Ergene Basin because its intensive industrial and agricultural activity makes water-quality monitoring especially important.

RiverGuard was inspired by the idea of combining scientific environmental data with citizen observations while keeping their roles clearly separated: photographs provide visual evidence, not chemical proof.

What it does
RiverGuard brings citizen reports, scientific measurements and environmental health risks into a shared monitoring platform.

Citizens can submit photographs, select a report category, add notes and location information, and later track their reports. An AI service performs preliminary moderation by checking whether an image is visually consistent with the selected category and provides a confidence score and explanation. Uncertain or non-visual cases are directed to human review.

The web platform provides maps, charts, monitoring locations, citizen reports and risk information for broader environmental monitoring.

RiverGuard also uses HL7 FHIR to represent environmental measurements and citizen reports as Observation resources and published health risks as RiskAssessment resources, demonstrating how environmental information could be exchanged with authorized health systems.

How we built it
RiverGuard was developed as an integrated mobile, web, backend, AI and health-data ecosystem.

The system uses a native Android application and web platform connected to a Spring Boot backend and PostgreSQL database. A FastAPI-based AI service performs image moderation, while a HAPI FHIR server stores standardized Observation and RiskAssessment resources. The services are deployed together using Docker Compose.

Both mobile and web applications consume the same backend API, keeping citizen reports, scientific observations, risk information and FHIR identifiers connected.

Challenges we ran into
One major challenge was combining scientific datasets from different studies without losing differences in units, sample types, detection limits and sources.

We also had to integrate independently developed mobile, web, backend, AI and FHIR components while maintaining a consistent API contract and authentication model.

Another important challenge was defining the AI system responsibly. We limited AI to visual consistency checks rather than chemical detection, retained human review for uncertain reports and ensured provider failures were not presented as verified evidence.

Accomplishments that we're proud of
RiverGuard became a working end-to-end system rather than just a prototype.

Citizens can register, sign in, submit reports and track their history. Reports are linked to authenticated accounts, evaluated by the AI service and connected to standardized FHIR resources.

We also transformed findings from four peer-reviewed studies into 207 structured environmental measurements and 9 health risk assessments while preserving their scientific context.

What we learned
We learned that environmental-health interoperability requires more than converting information into a common format. Scientific context, limitations and provenance must remain understandable.

The project gave us practical experience integrating Android, web, Spring Boot, PostgreSQL, AI and HL7 FHIR technologies into a single system.

Most importantly, we learned that responsible AI requires clearly defined limits, transparent uncertainty and human review.

What's next for RiverGuard
Our next goal is to complement literature-based data with verified laboratory results and live sensor measurements, enabling more continuous environmental monitoring.

We also plan to strengthen municipality review workflows, introduce notifications for emerging spatial and temporal risk patterns, support additional river basins and improve moderation and analytics.

In the long term, RiverGuard could become a reusable framework connecting communities, scientists and authorized institutions through shared environmental-health evidence.

Built With
coil
docker
fastapi
fhir
hl7
jetpack
kotlin
leaflet.js
maplibre
postgresql
python
react-native
recharts
spring
vite
Try it out
 ieee-hackathon-omega.vercel.app
 GitHub Repo
 drive.google.com
