# phabricator-k8s-plugin

This Jenkins plugin allows exposing [Phabricator Counduit Credentials](https://github.com/uber/phabricator-jenkins-plugin/blob/master/src/main/java/com/uber/jenkins/phabricator/credentials/ConduitCredentials.java)
as Kubernetes secrets to be consumed by the [Kubernetes credentials provider](https://github.com/jenkinsci/kubernetes-credentials-provider-plugin) plugin.

A sample secret is shown below:

```
apiVersion: v1
kind: Secret
metadata:
  # this is the jenkins id.
  name: "a-test-secret"
  labels:
    # so we know what type it is.
    "jenkins.io/credentials-type": "phabricatorConduit"
  annotations:
    # description - can not be a label as spaces are not allowed
    "jenkins.io/credentials-description" : "secret conduit credential from Kubernetes"
type: Opaque
data:
  # UTF-8 base64 encoded
  token: bXlTZWNyZXQh  # mySecret!
  url: https://phab.mydomain.com
```
