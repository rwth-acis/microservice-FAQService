--
-- Database Schema:  faq 
-- Automatically generated sql script for the service FAQService, created by the CAE.
-- --------------------------------------------------------

--
-- Table structure for table entry.
--
CREATE TABLE faq.entry (
  answer TEXT,
  ID int NOT NULL AUTO_INCREMENT,
  question TEXT,
  CONSTRAINT ID_PK PRIMARY KEY (ID)
);

