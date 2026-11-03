# Lab 12: Managing Configuration and Sensitive Data with ConfigMaps and Secrets

## Objective

Store non-sensitive MySQL configuration in a **ConfigMap** and sensitive MySQL credentials in a **Secret** (Base64-encoded in a YAML manifest), both in the `ivolve` namespace.

## Environment

- Kubernetes cluster built with kubeadm on VMware Workstation (Lab 9)
- `master-1` (control-plane) and `worker-1`, Ubuntu 26.04, Kubernetes v1.35.9, containerd 2.2.2, Calico CNI
- All commands were run on the master node

## Naming note

The lab text uses `ivolveCM` and `ivolveSecret`. Kubernetes resource names must be lowercase (RFC 1123), so the objects were created as:

| Lab name | Actual name |
|---|---|
| ivolveCM | `ivolvecm` |
| ivolveSecret | `ivolvesecret` |

## Step 1: Verify the namespace

```bash
kubectl get ns ivolve
```

## Step 2: Create the ConfigMap

`configmap.yaml`:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ivolvecm
  namespace: ivolve
data:
  DB_HOST: mysql
  DB_USER: ivolve_user
```

- `DB_HOST`: hostname of the MySQL StatefulSet's headless Service (`mysql`)
- `DB_USER`: application database user for the `ivolve` database (`ivolve_user`)

```bash
kubectl apply -f configmap.yaml
kubectl get cm ivolvecm -n ivolve -o yaml
```

## Step 3: Encode the secret values with Base64

```bash
echo -n 'RootPass123' | base64
echo -n 'AppPass123' | base64
```

Output:

```
Um9vdFBhc3MxMjM=
QXBwUGFzczEyMw==
```

`-n` prevents a trailing newline from being included in the encoded value.

## Step 4: Create the Secret

`secret.yaml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: ivolvesecret
  namespace: ivolve
type: Opaque
data:
  MYSQL_ROOT_PASSWORD: Um9vdFBhc3MxMjM=
  DB_PASSWORD: QXBwUGFzczEyMw==
```

- `MYSQL_ROOT_PASSWORD`: root password for the MySQL database
- `DB_PASSWORD`: password for the application user in `DB_USER`

```bash
kubectl apply -f secret.yaml
kubectl get secret ivolvesecret -n ivolve -o yaml
```

## Step 5: Verify the Base64 decoding

```bash
kubectl get secret ivolvesecret -n ivolve -o jsonpath='{.data.DB_PASSWORD}' | base64 -d; echo
```

Output:

```
AppPass123
```

## Result

- ConfigMap `ivolvecm` created in namespace `ivolve` with `DB_HOST` and `DB_USER`
- Secret `ivolvesecret` created in namespace `ivolve` (type `Opaque`) with `MYSQL_ROOT_PASSWORD` and `DB_PASSWORD`, Base64-encoded
- Decoding the stored value returns the original password

## Notes

- Base64 is an encoding, not encryption. Anyone with read access to the Secret can decode it.
- The passwords above are lab values only.

## Screenshots

- `screenshots/step1-configmap.png`
- `screenshots/step2-secret.png`
- `screenshots/step3-verify.png`
