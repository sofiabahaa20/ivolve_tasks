# Lab 11: Namespace Management and Resource Quota Enforcement

## Objective

- Create a Kubernetes namespace named `ivolve` to isolate application resources.
- Create a ResourceQuota in the `ivolve` namespace that limits the total number of pods to 3.
- Verify that the ResourceQuota is applied and that Kubernetes prevents the creation of additional pods when the limit is reached.

## Environment

| Item | Value |
|---|---|
| Cluster | kubeadm cluster from Lab 9 (Kubernetes v1.35.9) |
| Control plane | `master-1` |
| Worker | `worker-1` (tainted `workload=ivolveApp:NoSchedule` and labeled `project=ivolve` in Lab 10) |

## Files

| File | Purpose |
|---|---|
| `ivolve-quota.yaml` | ResourceQuota limiting the namespace to 3 pods |
| `ivolve-app.yaml` | Deployment with 3 nginx replicas used to reach the limit |

---

## Step 1: Create the namespace

```bash
kubectl create namespace ivolve
kubectl get namespace ivolve
```

Output:

```
namespace/ivolve created
NAME     STATUS   AGE
ivolve   Active   0s
```

**Screenshot:** `images/step_01_namespace.png`

## Step 2: Create the ResourceQuota

`ivolve-quota.yaml`:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: ivolve-quota
  namespace: ivolve
spec:
  hard:
    pods: "3"
```

```bash
kubectl apply -f ivolve-quota.yaml
```

Output:

```
resourcequota/ivolve-quota created
```

**Screenshot:** `images/step_02_quota.png`

## Step 3: Verify the ResourceQuota

```bash
kubectl get resourcequota -n ivolve
kubectl describe resourcequota ivolve-quota -n ivolve
```

Result: the quota shows `pods` with `Used 0` and `Hard 3`.

**Screenshot:** `images/step_03_verify_quota.png`

## Step 4: Create pods until the limit is reached

Worker-1 is tainted from Lab 10, so the pod template needs a toleration for `workload=ivolveApp:NoSchedule`. It also uses `nodeSelector: project: ivolve` to target the labeled node.

`ivolve-app.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ivolve-nginx
  namespace: ivolve
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ivolve-nginx
  template:
    metadata:
      labels:
        app: ivolve-nginx
    spec:
      tolerations:
      - key: "workload"
        operator: "Equal"
        value: "ivolveApp"
        effect: "NoSchedule"
      nodeSelector:
        project: ivolve
      containers:
      - name: nginx
        image: nginx
```

```bash
kubectl apply -f ivolve-app.yaml
kubectl get pods -n ivolve -o wide
kubectl describe resourcequota ivolve-quota -n ivolve
```

Result:

- Three pods are created and scheduled on `worker-1`.
- The quota shows `pods` with `Used 3` and `Hard 3`. The pods are counted against the quota from the moment they are created, even while they are still in `ContainerCreating`.

**Screenshots:** `images/step_04_deployment.png`, `images/step_04_pods_and_quota.png`

## Step 5: Try to create a fourth pod

```bash
kubectl run extra-pod --image=nginx -n ivolve
```

Kubernetes rejects the request:

```
Error from server (Forbidden): pods "extra-pod" is forbidden: exceeded quota: ivolve-quota, requested: pods=1, used: pods=3, limited: pods=3
```

Confirm that no extra pod was created and the quota is still full:

```bash
kubectl get pods -n ivolve
kubectl describe resourcequota ivolve-quota -n ivolve
```

Result: only the three `ivolve-nginx` pods exist (all `Running`), and the quota is still `3 / 3`.

**Screenshots:** `images/step_05_extra_pod_rejected.png`, `images/step_05_final_state.png`

---

## Notes

- Kubernetes object names must be lowercase (RFC 1123), so `ivolveQuota` from the task description is created as `ivolve-quota`.
- The quota counts pods per namespace, so the system pods in `kube-system` are not affected.
- To free the quota for other pods, delete the test Deployment: `kubectl delete deployment ivolve-nginx -n ivolve`.
