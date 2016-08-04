--
-- Database Schema:  faq 
-- Automatically generated sql script for the service FAQService, created by the CAE.
-- --------------------------------------------------------

--
-- Table structure for table entry.
--
CREATE TABLE faq.entry (
  answer text,
  ID int,
  question Text,
  CONSTRAINT ID_PK PRIMARY KEY (ID)
);

